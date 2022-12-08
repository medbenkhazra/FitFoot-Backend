import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ITerrain } from '../terrain.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../terrain.test-samples';

import { TerrainService } from './terrain.service';

const requireRestSample: ITerrain = {
  ...sampleWithRequiredData,
};

describe('Terrain Service', () => {
  let service: TerrainService;
  let httpMock: HttpTestingController;
  let expectedResult: ITerrain | ITerrain[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(TerrainService);
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

    it('should create a Terrain', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const terrain = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(terrain).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Terrain', () => {
      const terrain = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(terrain).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Terrain', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Terrain', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Terrain', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addTerrainToCollectionIfMissing', () => {
      it('should add a Terrain to an empty array', () => {
        const terrain: ITerrain = sampleWithRequiredData;
        expectedResult = service.addTerrainToCollectionIfMissing([], terrain);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(terrain);
      });

      it('should not add a Terrain to an array that contains it', () => {
        const terrain: ITerrain = sampleWithRequiredData;
        const terrainCollection: ITerrain[] = [
          {
            ...terrain,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addTerrainToCollectionIfMissing(terrainCollection, terrain);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Terrain to an array that doesn't contain it", () => {
        const terrain: ITerrain = sampleWithRequiredData;
        const terrainCollection: ITerrain[] = [sampleWithPartialData];
        expectedResult = service.addTerrainToCollectionIfMissing(terrainCollection, terrain);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(terrain);
      });

      it('should add only unique Terrain to an array', () => {
        const terrainArray: ITerrain[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const terrainCollection: ITerrain[] = [sampleWithRequiredData];
        expectedResult = service.addTerrainToCollectionIfMissing(terrainCollection, ...terrainArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const terrain: ITerrain = sampleWithRequiredData;
        const terrain2: ITerrain = sampleWithPartialData;
        expectedResult = service.addTerrainToCollectionIfMissing([], terrain, terrain2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(terrain);
        expect(expectedResult).toContain(terrain2);
      });

      it('should accept null and undefined values', () => {
        const terrain: ITerrain = sampleWithRequiredData;
        expectedResult = service.addTerrainToCollectionIfMissing([], null, terrain, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(terrain);
      });

      it('should return initial array if no Terrain is added', () => {
        const terrainCollection: ITerrain[] = [sampleWithRequiredData];
        expectedResult = service.addTerrainToCollectionIfMissing(terrainCollection, undefined, null);
        expect(expectedResult).toEqual(terrainCollection);
      });
    });

    describe('compareTerrain', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareTerrain(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareTerrain(entity1, entity2);
        const compareResult2 = service.compareTerrain(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareTerrain(entity1, entity2);
        const compareResult2 = service.compareTerrain(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareTerrain(entity1, entity2);
        const compareResult2 = service.compareTerrain(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
