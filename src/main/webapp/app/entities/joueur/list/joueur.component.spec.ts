import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { JoueurService } from '../service/joueur.service';

import { JoueurComponent } from './joueur.component';

describe('Joueur Management Component', () => {
  let comp: JoueurComponent;
  let fixture: ComponentFixture<JoueurComponent>;
  let service: JoueurService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'joueur', component: JoueurComponent }]), HttpClientTestingModule],
      declarations: [JoueurComponent],
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
      .overrideTemplate(JoueurComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(JoueurComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(JoueurService);

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
    expect(comp.joueurs?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to joueurService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getJoueurIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getJoueurIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
