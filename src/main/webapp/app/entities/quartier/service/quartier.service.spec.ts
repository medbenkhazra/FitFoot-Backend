import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IQuartier } from '../quartier.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../quartier.test-samples';

import { QuartierService } from './quartier.service';

const requireRestSample: IQuartier = {
  ...sampleWithRequiredData,
};

describe('Quartier Service', () => {
  let service: QuartierService;
  let httpMock: HttpTestingController;
  let expectedResult: IQuartier | IQuartier[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(QuartierService);
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

    it('should create a Quartier', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const quartier = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(quartier).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Quartier', () => {
      const quartier = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(quartier).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Quartier', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Quartier', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Quartier', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addQuartierToCollectionIfMissing', () => {
      it('should add a Quartier to an empty array', () => {
        const quartier: IQuartier = sampleWithRequiredData;
        expectedResult = service.addQuartierToCollectionIfMissing([], quartier);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(quartier);
      });

      it('should not add a Quartier to an array that contains it', () => {
        const quartier: IQuartier = sampleWithRequiredData;
        const quartierCollection: IQuartier[] = [
          {
            ...quartier,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addQuartierToCollectionIfMissing(quartierCollection, quartier);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Quartier to an array that doesn't contain it", () => {
        const quartier: IQuartier = sampleWithRequiredData;
        const quartierCollection: IQuartier[] = [sampleWithPartialData];
        expectedResult = service.addQuartierToCollectionIfMissing(quartierCollection, quartier);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(quartier);
      });

      it('should add only unique Quartier to an array', () => {
        const quartierArray: IQuartier[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const quartierCollection: IQuartier[] = [sampleWithRequiredData];
        expectedResult = service.addQuartierToCollectionIfMissing(quartierCollection, ...quartierArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const quartier: IQuartier = sampleWithRequiredData;
        const quartier2: IQuartier = sampleWithPartialData;
        expectedResult = service.addQuartierToCollectionIfMissing([], quartier, quartier2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(quartier);
        expect(expectedResult).toContain(quartier2);
      });

      it('should accept null and undefined values', () => {
        const quartier: IQuartier = sampleWithRequiredData;
        expectedResult = service.addQuartierToCollectionIfMissing([], null, quartier, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(quartier);
      });

      it('should return initial array if no Quartier is added', () => {
        const quartierCollection: IQuartier[] = [sampleWithRequiredData];
        expectedResult = service.addQuartierToCollectionIfMissing(quartierCollection, undefined, null);
        expect(expectedResult).toEqual(quartierCollection);
      });
    });

    describe('compareQuartier', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareQuartier(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareQuartier(entity1, entity2);
        const compareResult2 = service.compareQuartier(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareQuartier(entity1, entity2);
        const compareResult2 = service.compareQuartier(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareQuartier(entity1, entity2);
        const compareResult2 = service.compareQuartier(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
