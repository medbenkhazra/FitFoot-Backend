import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IComplexe } from '../complexe.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../complexe.test-samples';

import { ComplexeService } from './complexe.service';

const requireRestSample: IComplexe = {
  ...sampleWithRequiredData,
};

describe('Complexe Service', () => {
  let service: ComplexeService;
  let httpMock: HttpTestingController;
  let expectedResult: IComplexe | IComplexe[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ComplexeService);
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

    it('should create a Complexe', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const complexe = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(complexe).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Complexe', () => {
      const complexe = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(complexe).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Complexe', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Complexe', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Complexe', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addComplexeToCollectionIfMissing', () => {
      it('should add a Complexe to an empty array', () => {
        const complexe: IComplexe = sampleWithRequiredData;
        expectedResult = service.addComplexeToCollectionIfMissing([], complexe);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(complexe);
      });

      it('should not add a Complexe to an array that contains it', () => {
        const complexe: IComplexe = sampleWithRequiredData;
        const complexeCollection: IComplexe[] = [
          {
            ...complexe,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addComplexeToCollectionIfMissing(complexeCollection, complexe);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Complexe to an array that doesn't contain it", () => {
        const complexe: IComplexe = sampleWithRequiredData;
        const complexeCollection: IComplexe[] = [sampleWithPartialData];
        expectedResult = service.addComplexeToCollectionIfMissing(complexeCollection, complexe);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(complexe);
      });

      it('should add only unique Complexe to an array', () => {
        const complexeArray: IComplexe[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const complexeCollection: IComplexe[] = [sampleWithRequiredData];
        expectedResult = service.addComplexeToCollectionIfMissing(complexeCollection, ...complexeArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const complexe: IComplexe = sampleWithRequiredData;
        const complexe2: IComplexe = sampleWithPartialData;
        expectedResult = service.addComplexeToCollectionIfMissing([], complexe, complexe2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(complexe);
        expect(expectedResult).toContain(complexe2);
      });

      it('should accept null and undefined values', () => {
        const complexe: IComplexe = sampleWithRequiredData;
        expectedResult = service.addComplexeToCollectionIfMissing([], null, complexe, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(complexe);
      });

      it('should return initial array if no Complexe is added', () => {
        const complexeCollection: IComplexe[] = [sampleWithRequiredData];
        expectedResult = service.addComplexeToCollectionIfMissing(complexeCollection, undefined, null);
        expect(expectedResult).toEqual(complexeCollection);
      });
    });

    describe('compareComplexe', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareComplexe(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareComplexe(entity1, entity2);
        const compareResult2 = service.compareComplexe(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareComplexe(entity1, entity2);
        const compareResult2 = service.compareComplexe(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareComplexe(entity1, entity2);
        const compareResult2 = service.compareComplexe(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
