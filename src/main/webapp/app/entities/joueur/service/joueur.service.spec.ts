import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IJoueur } from '../joueur.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../joueur.test-samples';

import { JoueurService, RestJoueur } from './joueur.service';

const requireRestSample: RestJoueur = {
  ...sampleWithRequiredData,
  birthDay: sampleWithRequiredData.birthDay?.format(DATE_FORMAT),
};

describe('Joueur Service', () => {
  let service: JoueurService;
  let httpMock: HttpTestingController;
  let expectedResult: IJoueur | IJoueur[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(JoueurService);
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

    it('should create a Joueur', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const joueur = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(joueur).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Joueur', () => {
      const joueur = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(joueur).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Joueur', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Joueur', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Joueur', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addJoueurToCollectionIfMissing', () => {
      it('should add a Joueur to an empty array', () => {
        const joueur: IJoueur = sampleWithRequiredData;
        expectedResult = service.addJoueurToCollectionIfMissing([], joueur);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(joueur);
      });

      it('should not add a Joueur to an array that contains it', () => {
        const joueur: IJoueur = sampleWithRequiredData;
        const joueurCollection: IJoueur[] = [
          {
            ...joueur,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addJoueurToCollectionIfMissing(joueurCollection, joueur);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Joueur to an array that doesn't contain it", () => {
        const joueur: IJoueur = sampleWithRequiredData;
        const joueurCollection: IJoueur[] = [sampleWithPartialData];
        expectedResult = service.addJoueurToCollectionIfMissing(joueurCollection, joueur);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(joueur);
      });

      it('should add only unique Joueur to an array', () => {
        const joueurArray: IJoueur[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const joueurCollection: IJoueur[] = [sampleWithRequiredData];
        expectedResult = service.addJoueurToCollectionIfMissing(joueurCollection, ...joueurArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const joueur: IJoueur = sampleWithRequiredData;
        const joueur2: IJoueur = sampleWithPartialData;
        expectedResult = service.addJoueurToCollectionIfMissing([], joueur, joueur2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(joueur);
        expect(expectedResult).toContain(joueur2);
      });

      it('should accept null and undefined values', () => {
        const joueur: IJoueur = sampleWithRequiredData;
        expectedResult = service.addJoueurToCollectionIfMissing([], null, joueur, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(joueur);
      });

      it('should return initial array if no Joueur is added', () => {
        const joueurCollection: IJoueur[] = [sampleWithRequiredData];
        expectedResult = service.addJoueurToCollectionIfMissing(joueurCollection, undefined, null);
        expect(expectedResult).toEqual(joueurCollection);
      });
    });

    describe('compareJoueur', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareJoueur(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareJoueur(entity1, entity2);
        const compareResult2 = service.compareJoueur(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareJoueur(entity1, entity2);
        const compareResult2 = service.compareJoueur(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareJoueur(entity1, entity2);
        const compareResult2 = service.compareJoueur(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
