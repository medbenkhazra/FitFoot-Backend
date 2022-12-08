import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { TerrainFormService } from './terrain-form.service';
import { TerrainService } from '../service/terrain.service';
import { ITerrain } from '../terrain.model';
import { IComplexe } from 'app/entities/complexe/complexe.model';
import { ComplexeService } from 'app/entities/complexe/service/complexe.service';

import { TerrainUpdateComponent } from './terrain-update.component';

describe('Terrain Management Update Component', () => {
  let comp: TerrainUpdateComponent;
  let fixture: ComponentFixture<TerrainUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let terrainFormService: TerrainFormService;
  let terrainService: TerrainService;
  let complexeService: ComplexeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [TerrainUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(TerrainUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TerrainUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    terrainFormService = TestBed.inject(TerrainFormService);
    terrainService = TestBed.inject(TerrainService);
    complexeService = TestBed.inject(ComplexeService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Complexe query and add missing value', () => {
      const terrain: ITerrain = { id: 456 };
      const complexe: IComplexe = { id: 38682 };
      terrain.complexe = complexe;

      const complexeCollection: IComplexe[] = [{ id: 2223 }];
      jest.spyOn(complexeService, 'query').mockReturnValue(of(new HttpResponse({ body: complexeCollection })));
      const additionalComplexes = [complexe];
      const expectedCollection: IComplexe[] = [...additionalComplexes, ...complexeCollection];
      jest.spyOn(complexeService, 'addComplexeToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ terrain });
      comp.ngOnInit();

      expect(complexeService.query).toHaveBeenCalled();
      expect(complexeService.addComplexeToCollectionIfMissing).toHaveBeenCalledWith(
        complexeCollection,
        ...additionalComplexes.map(expect.objectContaining)
      );
      expect(comp.complexesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const terrain: ITerrain = { id: 456 };
      const complexe: IComplexe = { id: 81321 };
      terrain.complexe = complexe;

      activatedRoute.data = of({ terrain });
      comp.ngOnInit();

      expect(comp.complexesSharedCollection).toContain(complexe);
      expect(comp.terrain).toEqual(terrain);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITerrain>>();
      const terrain = { id: 123 };
      jest.spyOn(terrainFormService, 'getTerrain').mockReturnValue(terrain);
      jest.spyOn(terrainService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ terrain });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: terrain }));
      saveSubject.complete();

      // THEN
      expect(terrainFormService.getTerrain).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(terrainService.update).toHaveBeenCalledWith(expect.objectContaining(terrain));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITerrain>>();
      const terrain = { id: 123 };
      jest.spyOn(terrainFormService, 'getTerrain').mockReturnValue({ id: null });
      jest.spyOn(terrainService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ terrain: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: terrain }));
      saveSubject.complete();

      // THEN
      expect(terrainFormService.getTerrain).toHaveBeenCalled();
      expect(terrainService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITerrain>>();
      const terrain = { id: 123 };
      jest.spyOn(terrainService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ terrain });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(terrainService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareComplexe', () => {
      it('Should forward to complexeService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(complexeService, 'compareComplexe');
        comp.compareComplexe(entity, entity2);
        expect(complexeService.compareComplexe).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
