import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { TerrainService } from '../service/terrain.service';

import { TerrainComponent } from './terrain.component';

describe('Terrain Management Component', () => {
  let comp: TerrainComponent;
  let fixture: ComponentFixture<TerrainComponent>;
  let service: TerrainService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'terrain', component: TerrainComponent }]), HttpClientTestingModule],
      declarations: [TerrainComponent],
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
      .overrideTemplate(TerrainComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TerrainComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(TerrainService);

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
    expect(comp.terrains?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to terrainService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getTerrainIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getTerrainIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
