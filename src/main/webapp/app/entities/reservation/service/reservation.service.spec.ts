import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IReservation } from '../reservation.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../reservation.test-samples';

import { ReservationService, RestReservation } from './reservation.service';

const requireRestSample: RestReservation = {
  ...sampleWithRequiredData,
  date: sampleWithRequiredData.date?.format(DATE_FORMAT),
  heureDebut: sampleWithRequiredData.heureDebut?.toJSON(),
  heureFin: sampleWithRequiredData.heureFin?.toJSON(),
};

describe('Reservation Service', () => {
  let service: ReservationService;
  let httpMock: HttpTestingController;
  let expectedResult: IReservation | IReservation[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ReservationService);
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

    it('should create a Reservation', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const reservation = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(reservation).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Reservation', () => {
      const reservation = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(reservation).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Reservation', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Reservation', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Reservation', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addReservationToCollectionIfMissing', () => {
      it('should add a Reservation to an empty array', () => {
        const reservation: IReservation = sampleWithRequiredData;
        expectedResult = service.addReservationToCollectionIfMissing([], reservation);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(reservation);
      });

      it('should not add a Reservation to an array that contains it', () => {
        const reservation: IReservation = sampleWithRequiredData;
        const reservationCollection: IReservation[] = [
          {
            ...reservation,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addReservationToCollectionIfMissing(reservationCollection, reservation);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Reservation to an array that doesn't contain it", () => {
        const reservation: IReservation = sampleWithRequiredData;
        const reservationCollection: IReservation[] = [sampleWithPartialData];
        expectedResult = service.addReservationToCollectionIfMissing(reservationCollection, reservation);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(reservation);
      });

      it('should add only unique Reservation to an array', () => {
        const reservationArray: IReservation[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const reservationCollection: IReservation[] = [sampleWithRequiredData];
        expectedResult = service.addReservationToCollectionIfMissing(reservationCollection, ...reservationArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const reservation: IReservation = sampleWithRequiredData;
        const reservation2: IReservation = sampleWithPartialData;
        expectedResult = service.addReservationToCollectionIfMissing([], reservation, reservation2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(reservation);
        expect(expectedResult).toContain(reservation2);
      });

      it('should accept null and undefined values', () => {
        const reservation: IReservation = sampleWithRequiredData;
        expectedResult = service.addReservationToCollectionIfMissing([], null, reservation, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(reservation);
      });

      it('should return initial array if no Reservation is added', () => {
        const reservationCollection: IReservation[] = [sampleWithRequiredData];
        expectedResult = service.addReservationToCollectionIfMissing(reservationCollection, undefined, null);
        expect(expectedResult).toEqual(reservationCollection);
      });
    });

    describe('compareReservation', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareReservation(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareReservation(entity1, entity2);
        const compareResult2 = service.compareReservation(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareReservation(entity1, entity2);
        const compareResult2 = service.compareReservation(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareReservation(entity1, entity2);
        const compareResult2 = service.compareReservation(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
