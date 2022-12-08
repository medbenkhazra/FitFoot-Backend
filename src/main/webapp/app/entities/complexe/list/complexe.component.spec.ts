import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ComplexeService } from '../service/complexe.service';

import { ComplexeComponent } from './complexe.component';

describe('Complexe Management Component', () => {
  let comp: ComplexeComponent;
  let fixture: ComponentFixture<ComplexeComponent>;
  let service: ComplexeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'complexe', component: ComplexeComponent }]), HttpClientTestingModule],
      declarations: [ComplexeComponent],
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
      .overrideTemplate(ComplexeComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ComplexeComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(ComplexeService);

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
    expect(comp.complexes?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to complexeService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getComplexeIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getComplexeIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
