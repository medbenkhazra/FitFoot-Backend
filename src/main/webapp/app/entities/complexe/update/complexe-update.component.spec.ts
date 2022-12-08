import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ComplexeFormService } from './complexe-form.service';
import { ComplexeService } from '../service/complexe.service';
import { IComplexe } from '../complexe.model';
import { IQuartier } from 'app/entities/quartier/quartier.model';
import { QuartierService } from 'app/entities/quartier/service/quartier.service';
import { IProprietaire } from 'app/entities/proprietaire/proprietaire.model';
import { ProprietaireService } from 'app/entities/proprietaire/service/proprietaire.service';

import { ComplexeUpdateComponent } from './complexe-update.component';

describe('Complexe Management Update Component', () => {
  let comp: ComplexeUpdateComponent;
  let fixture: ComponentFixture<ComplexeUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let complexeFormService: ComplexeFormService;
  let complexeService: ComplexeService;
  let quartierService: QuartierService;
  let proprietaireService: ProprietaireService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ComplexeUpdateComponent],
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
      .overrideTemplate(ComplexeUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ComplexeUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    complexeFormService = TestBed.inject(ComplexeFormService);
    complexeService = TestBed.inject(ComplexeService);
    quartierService = TestBed.inject(QuartierService);
    proprietaireService = TestBed.inject(ProprietaireService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Quartier query and add missing value', () => {
      const complexe: IComplexe = { id: 456 };
      const quartier: IQuartier = { id: 77116 };
      complexe.quartier = quartier;

      const quartierCollection: IQuartier[] = [{ id: 19222 }];
      jest.spyOn(quartierService, 'query').mockReturnValue(of(new HttpResponse({ body: quartierCollection })));
      const additionalQuartiers = [quartier];
      const expectedCollection: IQuartier[] = [...additionalQuartiers, ...quartierCollection];
      jest.spyOn(quartierService, 'addQuartierToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ complexe });
      comp.ngOnInit();

      expect(quartierService.query).toHaveBeenCalled();
      expect(quartierService.addQuartierToCollectionIfMissing).toHaveBeenCalledWith(
        quartierCollection,
        ...additionalQuartiers.map(expect.objectContaining)
      );
      expect(comp.quartiersSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Proprietaire query and add missing value', () => {
      const complexe: IComplexe = { id: 456 };
      const proprietaire: IProprietaire = { id: 54691 };
      complexe.proprietaire = proprietaire;

      const proprietaireCollection: IProprietaire[] = [{ id: 21193 }];
      jest.spyOn(proprietaireService, 'query').mockReturnValue(of(new HttpResponse({ body: proprietaireCollection })));
      const additionalProprietaires = [proprietaire];
      const expectedCollection: IProprietaire[] = [...additionalProprietaires, ...proprietaireCollection];
      jest.spyOn(proprietaireService, 'addProprietaireToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ complexe });
      comp.ngOnInit();

      expect(proprietaireService.query).toHaveBeenCalled();
      expect(proprietaireService.addProprietaireToCollectionIfMissing).toHaveBeenCalledWith(
        proprietaireCollection,
        ...additionalProprietaires.map(expect.objectContaining)
      );
      expect(comp.proprietairesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const complexe: IComplexe = { id: 456 };
      const quartier: IQuartier = { id: 71433 };
      complexe.quartier = quartier;
      const proprietaire: IProprietaire = { id: 60204 };
      complexe.proprietaire = proprietaire;

      activatedRoute.data = of({ complexe });
      comp.ngOnInit();

      expect(comp.quartiersSharedCollection).toContain(quartier);
      expect(comp.proprietairesSharedCollection).toContain(proprietaire);
      expect(comp.complexe).toEqual(complexe);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IComplexe>>();
      const complexe = { id: 123 };
      jest.spyOn(complexeFormService, 'getComplexe').mockReturnValue(complexe);
      jest.spyOn(complexeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ complexe });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: complexe }));
      saveSubject.complete();

      // THEN
      expect(complexeFormService.getComplexe).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(complexeService.update).toHaveBeenCalledWith(expect.objectContaining(complexe));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IComplexe>>();
      const complexe = { id: 123 };
      jest.spyOn(complexeFormService, 'getComplexe').mockReturnValue({ id: null });
      jest.spyOn(complexeService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ complexe: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: complexe }));
      saveSubject.complete();

      // THEN
      expect(complexeFormService.getComplexe).toHaveBeenCalled();
      expect(complexeService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IComplexe>>();
      const complexe = { id: 123 };
      jest.spyOn(complexeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ complexe });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(complexeService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareQuartier', () => {
      it('Should forward to quartierService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(quartierService, 'compareQuartier');
        comp.compareQuartier(entity, entity2);
        expect(quartierService.compareQuartier).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareProprietaire', () => {
      it('Should forward to proprietaireService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(proprietaireService, 'compareProprietaire');
        comp.compareProprietaire(entity, entity2);
        expect(proprietaireService.compareProprietaire).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
