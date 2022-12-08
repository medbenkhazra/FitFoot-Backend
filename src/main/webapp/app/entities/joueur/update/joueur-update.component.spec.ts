import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { JoueurFormService } from './joueur-form.service';
import { JoueurService } from '../service/joueur.service';
import { IJoueur } from '../joueur.model';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IEquipe } from 'app/entities/equipe/equipe.model';
import { EquipeService } from 'app/entities/equipe/service/equipe.service';
import { IQuartier } from 'app/entities/quartier/quartier.model';
import { QuartierService } from 'app/entities/quartier/service/quartier.service';

import { JoueurUpdateComponent } from './joueur-update.component';

describe('Joueur Management Update Component', () => {
  let comp: JoueurUpdateComponent;
  let fixture: ComponentFixture<JoueurUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let joueurFormService: JoueurFormService;
  let joueurService: JoueurService;
  let userService: UserService;
  let equipeService: EquipeService;
  let quartierService: QuartierService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [JoueurUpdateComponent],
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
      .overrideTemplate(JoueurUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(JoueurUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    joueurFormService = TestBed.inject(JoueurFormService);
    joueurService = TestBed.inject(JoueurService);
    userService = TestBed.inject(UserService);
    equipeService = TestBed.inject(EquipeService);
    quartierService = TestBed.inject(QuartierService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const joueur: IJoueur = { id: 456 };
      const user: IUser = { id: 62763 };
      joueur.user = user;

      const userCollection: IUser[] = [{ id: 54339 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [user];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ joueur });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining)
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Equipe query and add missing value', () => {
      const joueur: IJoueur = { id: 456 };
      const equipes: IEquipe[] = [{ id: 13711 }];
      joueur.equipes = equipes;

      const equipeCollection: IEquipe[] = [{ id: 21472 }];
      jest.spyOn(equipeService, 'query').mockReturnValue(of(new HttpResponse({ body: equipeCollection })));
      const additionalEquipes = [...equipes];
      const expectedCollection: IEquipe[] = [...additionalEquipes, ...equipeCollection];
      jest.spyOn(equipeService, 'addEquipeToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ joueur });
      comp.ngOnInit();

      expect(equipeService.query).toHaveBeenCalled();
      expect(equipeService.addEquipeToCollectionIfMissing).toHaveBeenCalledWith(
        equipeCollection,
        ...additionalEquipes.map(expect.objectContaining)
      );
      expect(comp.equipesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Quartier query and add missing value', () => {
      const joueur: IJoueur = { id: 456 };
      const quartier: IQuartier = { id: 93147 };
      joueur.quartier = quartier;

      const quartierCollection: IQuartier[] = [{ id: 5708 }];
      jest.spyOn(quartierService, 'query').mockReturnValue(of(new HttpResponse({ body: quartierCollection })));
      const additionalQuartiers = [quartier];
      const expectedCollection: IQuartier[] = [...additionalQuartiers, ...quartierCollection];
      jest.spyOn(quartierService, 'addQuartierToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ joueur });
      comp.ngOnInit();

      expect(quartierService.query).toHaveBeenCalled();
      expect(quartierService.addQuartierToCollectionIfMissing).toHaveBeenCalledWith(
        quartierCollection,
        ...additionalQuartiers.map(expect.objectContaining)
      );
      expect(comp.quartiersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const joueur: IJoueur = { id: 456 };
      const user: IUser = { id: 76617 };
      joueur.user = user;
      const equipe: IEquipe = { id: 34450 };
      joueur.equipes = [equipe];
      const quartier: IQuartier = { id: 95452 };
      joueur.quartier = quartier;

      activatedRoute.data = of({ joueur });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContain(user);
      expect(comp.equipesSharedCollection).toContain(equipe);
      expect(comp.quartiersSharedCollection).toContain(quartier);
      expect(comp.joueur).toEqual(joueur);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IJoueur>>();
      const joueur = { id: 123 };
      jest.spyOn(joueurFormService, 'getJoueur').mockReturnValue(joueur);
      jest.spyOn(joueurService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ joueur });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: joueur }));
      saveSubject.complete();

      // THEN
      expect(joueurFormService.getJoueur).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(joueurService.update).toHaveBeenCalledWith(expect.objectContaining(joueur));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IJoueur>>();
      const joueur = { id: 123 };
      jest.spyOn(joueurFormService, 'getJoueur').mockReturnValue({ id: null });
      jest.spyOn(joueurService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ joueur: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: joueur }));
      saveSubject.complete();

      // THEN
      expect(joueurFormService.getJoueur).toHaveBeenCalled();
      expect(joueurService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IJoueur>>();
      const joueur = { id: 123 };
      jest.spyOn(joueurService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ joueur });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(joueurService.update).toHaveBeenCalled();
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

    describe('compareEquipe', () => {
      it('Should forward to equipeService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(equipeService, 'compareEquipe');
        comp.compareEquipe(entity, entity2);
        expect(equipeService.compareEquipe).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareQuartier', () => {
      it('Should forward to quartierService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(quartierService, 'compareQuartier');
        comp.compareQuartier(entity, entity2);
        expect(quartierService.compareQuartier).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
