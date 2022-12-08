import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { AnnonceFormService } from './annonce-form.service';
import { AnnonceService } from '../service/annonce.service';
import { IAnnonce } from '../annonce.model';
import { ITerrain } from 'app/entities/terrain/terrain.model';
import { TerrainService } from 'app/entities/terrain/service/terrain.service';
import { IJoueur } from 'app/entities/joueur/joueur.model';
import { JoueurService } from 'app/entities/joueur/service/joueur.service';

import { AnnonceUpdateComponent } from './annonce-update.component';

describe('Annonce Management Update Component', () => {
  let comp: AnnonceUpdateComponent;
  let fixture: ComponentFixture<AnnonceUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let annonceFormService: AnnonceFormService;
  let annonceService: AnnonceService;
  let terrainService: TerrainService;
  let joueurService: JoueurService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [AnnonceUpdateComponent],
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
      .overrideTemplate(AnnonceUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(AnnonceUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    annonceFormService = TestBed.inject(AnnonceFormService);
    annonceService = TestBed.inject(AnnonceService);
    terrainService = TestBed.inject(TerrainService);
    joueurService = TestBed.inject(JoueurService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call terrain query and add missing value', () => {
      const annonce: IAnnonce = { id: 456 };
      const terrain: ITerrain = { id: 15564 };
      annonce.terrain = terrain;

      const terrainCollection: ITerrain[] = [{ id: 36758 }];
      jest.spyOn(terrainService, 'query').mockReturnValue(of(new HttpResponse({ body: terrainCollection })));
      const expectedCollection: ITerrain[] = [terrain, ...terrainCollection];
      jest.spyOn(terrainService, 'addTerrainToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ annonce });
      comp.ngOnInit();

      expect(terrainService.query).toHaveBeenCalled();
      expect(terrainService.addTerrainToCollectionIfMissing).toHaveBeenCalledWith(terrainCollection, terrain);
      expect(comp.terrainsCollection).toEqual(expectedCollection);
    });

    it('Should call Joueur query and add missing value', () => {
      const annonce: IAnnonce = { id: 456 };
      const responsable: IJoueur = { id: 73331 };
      annonce.responsable = responsable;

      const joueurCollection: IJoueur[] = [{ id: 32419 }];
      jest.spyOn(joueurService, 'query').mockReturnValue(of(new HttpResponse({ body: joueurCollection })));
      const additionalJoueurs = [responsable];
      const expectedCollection: IJoueur[] = [...additionalJoueurs, ...joueurCollection];
      jest.spyOn(joueurService, 'addJoueurToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ annonce });
      comp.ngOnInit();

      expect(joueurService.query).toHaveBeenCalled();
      expect(joueurService.addJoueurToCollectionIfMissing).toHaveBeenCalledWith(
        joueurCollection,
        ...additionalJoueurs.map(expect.objectContaining)
      );
      expect(comp.joueursSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const annonce: IAnnonce = { id: 456 };
      const terrain: ITerrain = { id: 7161 };
      annonce.terrain = terrain;
      const responsable: IJoueur = { id: 5453 };
      annonce.responsable = responsable;

      activatedRoute.data = of({ annonce });
      comp.ngOnInit();

      expect(comp.terrainsCollection).toContain(terrain);
      expect(comp.joueursSharedCollection).toContain(responsable);
      expect(comp.annonce).toEqual(annonce);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IAnnonce>>();
      const annonce = { id: 123 };
      jest.spyOn(annonceFormService, 'getAnnonce').mockReturnValue(annonce);
      jest.spyOn(annonceService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ annonce });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: annonce }));
      saveSubject.complete();

      // THEN
      expect(annonceFormService.getAnnonce).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(annonceService.update).toHaveBeenCalledWith(expect.objectContaining(annonce));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IAnnonce>>();
      const annonce = { id: 123 };
      jest.spyOn(annonceFormService, 'getAnnonce').mockReturnValue({ id: null });
      jest.spyOn(annonceService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ annonce: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: annonce }));
      saveSubject.complete();

      // THEN
      expect(annonceFormService.getAnnonce).toHaveBeenCalled();
      expect(annonceService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IAnnonce>>();
      const annonce = { id: 123 };
      jest.spyOn(annonceService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ annonce });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(annonceService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareTerrain', () => {
      it('Should forward to terrainService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(terrainService, 'compareTerrain');
        comp.compareTerrain(entity, entity2);
        expect(terrainService.compareTerrain).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareJoueur', () => {
      it('Should forward to joueurService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(joueurService, 'compareJoueur');
        comp.compareJoueur(entity, entity2);
        expect(joueurService.compareJoueur).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
