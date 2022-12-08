import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IVille } from '../ville.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../ville.test-samples';

import { VilleService } from './ville.service';

const requireRestSample: IVille = {
  ...sampleWithRequiredData,
};

describe('Ville Service', () => {
  let service: VilleService;
  let httpMock: HttpTestingController;
  let expectedResult: IVille | IVille[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(VilleService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a Ville', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const ville = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(ville).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Ville', () => {
      const ville = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(ville).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Ville', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Ville', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Ville', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addVilleToCollectionIfMissing', () => {
      it('should add a Ville to an empty array', () => {
        const ville: IVille = sampleWithRequiredData;
        expectedResult = service.addVilleToCollectionIfMissing([], ville);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(ville);
      });

      it('should not add a Ville to an array that contains it', () => {
        const ville: IVille = sampleWithRequiredData;
        const villeCollection: IVille[] = [
          {
            ...ville,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addVilleToCollectionIfMissing(villeCollection, ville);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Ville to an array that doesn't contain it", () => {
        const ville: IVille = sampleWithRequiredData;
        const villeCollection: IVille[] = [sampleWithPartialData];
        expectedResult = service.addVilleToCollectionIfMissing(villeCollection, ville);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(ville);
      });

      it('should add only unique Ville to an array', () => {
        const villeArray: IVille[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const villeCollection: IVille[] = [sampleWithRequiredData];
        expectedResult = service.addVilleToCollectionIfMissing(villeCollection, ...villeArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const ville: IVille = sampleWithRequiredData;
        const ville2: IVille = sampleWithPartialData;
        expectedResult = service.addVilleToCollectionIfMissing([], ville, ville2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(ville);
        expect(expectedResult).toContain(ville2);
      });

      it('should accept null and undefined values', () => {
        const ville: IVille = sampleWithRequiredData;
        expectedResult = service.addVilleToCollectionIfMissing([], null, ville, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(ville);
      });

      it('should return initial array if no Ville is added', () => {
        const villeCollection: IVille[] = [sampleWithRequiredData];
        expectedResult = service.addVilleToCollectionIfMissing(villeCollection, undefined, null);
        expect(expectedResult).toEqual(villeCollection);
      });
    });

    describe('compareVille', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareVille(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareVille(entity1, entity2);
        const compareResult2 = service.compareVille(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareVille(entity1, entity2);
        const compareResult2 = service.compareVille(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareVille(entity1, entity2);
        const compareResult2 = service.compareVille(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
