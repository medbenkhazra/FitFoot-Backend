import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { QuartierFormService } from './quartier-form.service';
import { QuartierService } from '../service/quartier.service';
import { IQuartier } from '../quartier.model';
import { IVille } from 'app/entities/ville/ville.model';
import { VilleService } from 'app/entities/ville/service/ville.service';

import { QuartierUpdateComponent } from './quartier-update.component';

describe('Quartier Management Update Component', () => {
  let comp: QuartierUpdateComponent;
  let fixture: ComponentFixture<QuartierUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let quartierFormService: QuartierFormService;
  let quartierService: QuartierService;
  let villeService: VilleService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [QuartierUpdateComponent],
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
      .overrideTemplate(QuartierUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(QuartierUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    quartierFormService = TestBed.inject(QuartierFormService);
    quartierService = TestBed.inject(QuartierService);
    villeService = TestBed.inject(VilleService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Ville query and add missing value', () => {
      const quartier: IQuartier = { id: 456 };
      const ville: IVille = { id: 52697 };
      quartier.ville = ville;

      const villeCollection: IVille[] = [{ id: 71410 }];
      jest.spyOn(villeService, 'query').mockReturnValue(of(new HttpResponse({ body: villeCollection })));
      const additionalVilles = [ville];
      const expectedCollection: IVille[] = [...additionalVilles, ...villeCollection];
      jest.spyOn(villeService, 'addVilleToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ quartier });
      comp.ngOnInit();

      expect(villeService.query).toHaveBeenCalled();
      expect(villeService.addVilleToCollectionIfMissing).toHaveBeenCalledWith(
        villeCollection,
        ...additionalVilles.map(expect.objectContaining)
      );
      expect(comp.villesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const quartier: IQuartier = { id: 456 };
      const ville: IVille = { id: 87918 };
      quartier.ville = ville;

      activatedRoute.data = of({ quartier });
      comp.ngOnInit();

      expect(comp.villesSharedCollection).toContain(ville);
      expect(comp.quartier).toEqual(quartier);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IQuartier>>();
      const quartier = { id: 123 };
      jest.spyOn(quartierFormService, 'getQuartier').mockReturnValue(quartier);
      jest.spyOn(quartierService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ quartier });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: quartier }));
      saveSubject.complete();

      // THEN
      expect(quartierFormService.getQuartier).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(quartierService.update).toHaveBeenCalledWith(expect.objectContaining(quartier));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IQuartier>>();
      const quartier = { id: 123 };
      jest.spyOn(quartierFormService, 'getQuartier').mockReturnValue({ id: null });
      jest.spyOn(quartierService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ quartier: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: quartier }));
      saveSubject.complete();

      // THEN
      expect(quartierFormService.getQuartier).toHaveBeenCalled();
      expect(quartierService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IQuartier>>();
      const quartier = { id: 123 };
      jest.spyOn(quartierService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ quartier });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(quartierService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareVille', () => {
      it('Should forward to villeService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(villeService, 'compareVille');
        comp.compareVille(entity, entity2);
        expect(villeService.compareVille).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
