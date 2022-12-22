import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { EquipeFormService } from './equipe-form.service';
import { EquipeService } from '../service/equipe.service';
import { IEquipe } from '../equipe.model';

import { EquipeUpdateComponent } from './equipe-update.component';

describe('Equipe Management Update Component', () => {
  let comp: EquipeUpdateComponent;
  let fixture: ComponentFixture<EquipeUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let equipeFormService: EquipeFormService;
  let equipeService: EquipeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [EquipeUpdateComponent],
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
      .overrideTemplate(EquipeUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(EquipeUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    equipeFormService = TestBed.inject(EquipeFormService);
    equipeService = TestBed.inject(EquipeService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const equipe: IEquipe = { id: 456 };

      activatedRoute.data = of({ equipe });
      comp.ngOnInit();

      expect(comp.equipe).toEqual(equipe);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IEquipe>>();
      const equipe = { id: 123 };
      jest.spyOn(equipeFormService, 'getEquipe').mockReturnValue(equipe);
      jest.spyOn(equipeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ equipe });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: equipe }));
      saveSubject.complete();

      // THEN
      expect(equipeFormService.getEquipe).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(equipeService.update).toHaveBeenCalledWith(expect.objectContaining(equipe));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IEquipe>>();
      const equipe = { id: 123 };
      jest.spyOn(equipeFormService, 'getEquipe').mockReturnValue({ id: null });
      jest.spyOn(equipeService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ equipe: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: equipe }));
      saveSubject.complete();

      // THEN
      expect(equipeFormService.getEquipe).toHaveBeenCalled();
      expect(equipeService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IEquipe>>();
      const equipe = { id: 123 };
      jest.spyOn(equipeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ equipe });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(equipeService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
