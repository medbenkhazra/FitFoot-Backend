import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ProprietaireFormService } from './proprietaire-form.service';
import { ProprietaireService } from '../service/proprietaire.service';
import { IProprietaire } from '../proprietaire.model';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

import { ProprietaireUpdateComponent } from './proprietaire-update.component';

describe('Proprietaire Management Update Component', () => {
  let comp: ProprietaireUpdateComponent;
  let fixture: ComponentFixture<ProprietaireUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let proprietaireFormService: ProprietaireFormService;
  let proprietaireService: ProprietaireService;
  let userService: UserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ProprietaireUpdateComponent],
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
      .overrideTemplate(ProprietaireUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ProprietaireUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    proprietaireFormService = TestBed.inject(ProprietaireFormService);
    proprietaireService = TestBed.inject(ProprietaireService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const proprietaire: IProprietaire = { id: 456 };
      const user: IUser = { id: 42961 };
      proprietaire.user = user;

      const userCollection: IUser[] = [{ id: 64042 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [user];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ proprietaire });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining)
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const proprietaire: IProprietaire = { id: 456 };
      const user: IUser = { id: 80751 };
      proprietaire.user = user;

      activatedRoute.data = of({ proprietaire });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContain(user);
      expect(comp.proprietaire).toEqual(proprietaire);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProprietaire>>();
      const proprietaire = { id: 123 };
      jest.spyOn(proprietaireFormService, 'getProprietaire').mockReturnValue(proprietaire);
      jest.spyOn(proprietaireService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ proprietaire });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: proprietaire }));
      saveSubject.complete();

      // THEN
      expect(proprietaireFormService.getProprietaire).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(proprietaireService.update).toHaveBeenCalledWith(expect.objectContaining(proprietaire));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProprietaire>>();
      const proprietaire = { id: 123 };
      jest.spyOn(proprietaireFormService, 'getProprietaire').mockReturnValue({ id: null });
      jest.spyOn(proprietaireService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ proprietaire: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: proprietaire }));
      saveSubject.complete();

      // THEN
      expect(proprietaireFormService.getProprietaire).toHaveBeenCalled();
      expect(proprietaireService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProprietaire>>();
      const proprietaire = { id: 123 };
      jest.spyOn(proprietaireService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ proprietaire });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(proprietaireService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareUser', () => {
      it('Should forward to userService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(userService, 'compareUser');
        comp.compareUser(entity, entity2);
        expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
