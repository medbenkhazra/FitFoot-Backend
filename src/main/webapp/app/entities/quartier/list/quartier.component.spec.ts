import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { QuartierService } from '../service/quartier.service';

import { QuartierComponent } from './quartier.component';

describe('Quartier Management Component', () => {
  let comp: QuartierComponent;
  let fixture: ComponentFixture<QuartierComponent>;
  let service: QuartierService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'quartier', component: QuartierComponent }]), HttpClientTestingModule],
      declarations: [QuartierComponent],
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
      .overrideTemplate(QuartierComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(QuartierComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(QuartierService);

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
    expect(comp.quartiers?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to quartierService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getQuartierIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getQuartierIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
