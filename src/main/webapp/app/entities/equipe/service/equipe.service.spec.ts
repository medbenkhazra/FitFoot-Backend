import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IEquipe } from '../equipe.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../equipe.test-samples';

import { EquipeService } from './equipe.service';

const requireRestSample: IEquipe = {
  ...sampleWithRequiredData,
};

describe('Equipe Service', () => {
  let service: EquipeService;
  let httpMock: HttpTestingController;
  let expectedResult: IEquipe | IEquipe[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(EquipeService);
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

    it('should create a Equipe', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const equipe = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(equipe).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Equipe', () => {
      const equipe = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(equipe).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Equipe', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Equipe', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Equipe', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addEquipeToCollectionIfMissing', () => {
      it('should add a Equipe to an empty array', () => {
        const equipe: IEquipe = sampleWithRequiredData;
        expectedResult = service.addEquipeToCollectionIfMissing([], equipe);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(equipe);
      });

      it('should not add a Equipe to an array that contains it', () => {
        const equipe: IEquipe = sampleWithRequiredData;
        const equipeCollection: IEquipe[] = [
          {
            ...equipe,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addEquipeToCollectionIfMissing(equipeCollection, equipe);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Equipe to an array that doesn't contain it", () => {
        const equipe: IEquipe = sampleWithRequiredData;
        const equipeCollection: IEquipe[] = [sampleWithPartialData];
        expectedResult = service.addEquipeToCollectionIfMissing(equipeCollection, equipe);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(equipe);
      });

      it('should add only unique Equipe to an array', () => {
        const equipeArray: IEquipe[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const equipeCollection: IEquipe[] = [sampleWithRequiredData];
        expectedResult = service.addEquipeToCollectionIfMissing(equipeCollection, ...equipeArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const equipe: IEquipe = sampleWithRequiredData;
        const equipe2: IEquipe = sampleWithPartialData;
        expectedResult = service.addEquipeToCollectionIfMissing([], equipe, equipe2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(equipe);
        expect(expectedResult).toContain(equipe2);
      });

      it('should accept null and undefined values', () => {
        const equipe: IEquipe = sampleWithRequiredData;
        expectedResult = service.addEquipeToCollectionIfMissing([], null, equipe, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(equipe);
      });

      it('should return initial array if no Equipe is added', () => {
        const equipeCollection: IEquipe[] = [sampleWithRequiredData];
        expectedResult = service.addEquipeToCollectionIfMissing(equipeCollection, undefined, null);
        expect(expectedResult).toEqual(equipeCollection);
      });
    });

    describe('compareEquipe', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareEquipe(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareEquipe(entity1, entity2);
        const compareResult2 = service.compareEquipe(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareEquipe(entity1, entity2);
        const compareResult2 = service.compareEquipe(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareEquipe(entity1, entity2);
        const compareResult2 = service.compareEquipe(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
