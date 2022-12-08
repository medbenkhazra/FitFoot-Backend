import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { EquipeDetailComponent } from './equipe-detail.component';

describe('Equipe Management Detail Component', () => {
  let comp: EquipeDetailComponent;
  let fixture: ComponentFixture<EquipeDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [EquipeDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ equipe: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(EquipeDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(EquipeDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load equipe on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.equipe).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
