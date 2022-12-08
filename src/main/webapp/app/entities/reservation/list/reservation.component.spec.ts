import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ReservationService } from '../service/reservation.service';

import { ReservationComponent } from './reservation.component';

describe('Reservation Management Component', () => {
  let comp: ReservationComponent;
  let fixture: ComponentFixture<ReservationComponent>;
  let service: ReservationService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'reservation', component: ReservationComponent }]), HttpClientTestingModule],
      declarations: [ReservationComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            data: of({
              defaultSort: 'id,asc',
            }),
            queryParamMap: of(
              jest.requireActual('@angular/router').convertToParamMap({
                page: '1',
                size: '1',
                sort: 'id,desc',
              })
            ),
            snapshot: { queryParams: {} },
          },
        },
      ],
    })
      .overrideTemplate(ReservationComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ReservationComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(ReservationService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 123 }],
          headers,
        })
      )
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.reservations?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to reservationService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getReservationIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getReservationIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
