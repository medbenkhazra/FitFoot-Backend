import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { VilleFormService } from './ville-form.service';
import { VilleService } from '../service/ville.service';
import { IVille } from '../ville.model';

import { VilleUpdateComponent } from './ville-update.component';

describe('Ville Management Update Component', () => {
  let comp: VilleUpdateComponent;
  let fixture: ComponentFixture<VilleUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let villeFormService: VilleFormService;
  let villeService: VilleService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [VilleUpdateComponent],
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
      .overrideTemplate(VilleUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(VilleUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    villeFormService = TestBed.inject(VilleFormService);
    villeService = TestBed.inject(VilleService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const ville: IVille = { id: 456 };

      activatedRoute.data = of({ ville });
      comp.ngOnInit();

      expect(comp.ville).toEqual(ville);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IVille>>();
      const ville = { id: 123 };
      jest.spyOn(villeFormService, 'getVille').mockReturnValue(ville);
      jest.spyOn(villeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ville });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: ville }));
      saveSubject.complete();

      // THEN
      expect(villeFormService.getVille).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(villeService.update).toHaveBeenCalledWith(expect.objectContaining(ville));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IVille>>();
      const ville = { id: 123 };
      jest.spyOn(villeFormService, 'getVille').mockReturnValue({ id: null });
      jest.spyOn(villeService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ville: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: ville }));
      saveSubject.complete();

      // THEN
      expect(villeFormService.getVille).toHaveBeenCalled();
      expect(villeService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IVille>>();
      const ville = { id: 123 };
      jest.spyOn(villeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ville });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(villeService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
