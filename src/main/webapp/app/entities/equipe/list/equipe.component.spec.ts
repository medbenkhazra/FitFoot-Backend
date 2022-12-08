import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { EquipeService } from '../service/equipe.service';

import { EquipeComponent } from './equipe.component';

describe('Equipe Management Component', () => {
  let comp: EquipeComponent;
  let fixture: ComponentFixture<EquipeComponent>;
  let service: EquipeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'equipe', component: EquipeComponent }]), HttpClientTestingModule],
      declarations: [EquipeComponent],
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
      .overrideTemplate(EquipeComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(EquipeComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(EquipeService);

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
    expect(comp.equipes?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to equipeService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getEquipeIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getEquipeIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
