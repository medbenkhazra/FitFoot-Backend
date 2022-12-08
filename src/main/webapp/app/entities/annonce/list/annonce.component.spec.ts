import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { AnnonceService } from '../service/annonce.service';

import { AnnonceComponent } from './annonce.component';

describe('Annonce Management Component', () => {
  let comp: AnnonceComponent;
  let fixture: ComponentFixture<AnnonceComponent>;
  let service: AnnonceService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'annonce', component: AnnonceComponent }]), HttpClientTestingModule],
      declarations: [AnnonceComponent],
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
      .overrideTemplate(AnnonceComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(AnnonceComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(AnnonceService);

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
    expect(comp.annonces?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to annonceService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getAnnonceIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getAnnonceIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
