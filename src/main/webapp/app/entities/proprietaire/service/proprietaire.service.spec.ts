import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IProprietaire } from '../proprietaire.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../proprietaire.test-samples';

import { ProprietaireService } from './proprietaire.service';

const requireRestSample: IProprietaire = {
  ...sampleWithRequiredData,
};

describe('Proprietaire Service', () => {
  let service: ProprietaireService;
  let httpMock: HttpTestingController;
  let expectedResult: IProprietaire | IProprietaire[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ProprietaireService);
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

    it('should create a Proprietaire', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const proprietaire = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(proprietaire).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Proprietaire', () => {
      const proprietaire = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(proprietaire).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Proprietaire', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Proprietaire', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Proprietaire', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addProprietaireToCollectionIfMissing', () => {
      it('should add a Proprietaire to an empty array', () => {
        const proprietaire: IProprietaire = sampleWithRequiredData;
        expectedResult = service.addProprietaireToCollectionIfMissing([], proprietaire);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(proprietaire);
      });

      it('should not add a Proprietaire to an array that contains it', () => {
        const proprietaire: IProprietaire = sampleWithRequiredData;
        const proprietaireCollection: IProprietaire[] = [
          {
            ...proprietaire,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addProprietaireToCollectionIfMissing(proprietaireCollection, proprietaire);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Proprietaire to an array that doesn't contain it", () => {
        const proprietaire: IProprietaire = sampleWithRequiredData;
        const proprietaireCollection: IProprietaire[] = [sampleWithPartialData];
        expectedResult = service.addProprietaireToCollectionIfMissing(proprietaireCollection, proprietaire);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(proprietaire);
      });

      it('should add only unique Proprietaire to an array', () => {
        const proprietaireArray: IProprietaire[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const proprietaireCollection: IProprietaire[] = [sampleWithRequiredData];
        expectedResult = service.addProprietaireToCollectionIfMissing(proprietaireCollection, ...proprietaireArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const proprietaire: IProprietaire = sampleWithRequiredData;
        const proprietaire2: IProprietaire = sampleWithPartialData;
        expectedResult = service.addProprietaireToCollectionIfMissing([], proprietaire, proprietaire2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(proprietaire);
        expect(expectedResult).toContain(proprietaire2);
      });

      it('should accept null and undefined values', () => {
        const proprietaire: IProprietaire = sampleWithRequiredData;
        expectedResult = service.addProprietaireToCollectionIfMissing([], null, proprietaire, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(proprietaire);
      });

      it('should return initial array if no Proprietaire is added', () => {
        const proprietaireCollection: IProprietaire[] = [sampleWithRequiredData];
        expectedResult = service.addProprietaireToCollectionIfMissing(proprietaireCollection, undefined, null);
        expect(expectedResult).toEqual(proprietaireCollection);
      });
    });

    describe('compareProprietaire', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareProprietaire(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareProprietaire(entity1, entity2);
        const compareResult2 = service.compareProprietaire(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareProprietaire(entity1, entity2);
        const compareResult2 = service.compareProprietaire(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareProprietaire(entity1, entity2);
        const compareResult2 = service.compareProprietaire(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
