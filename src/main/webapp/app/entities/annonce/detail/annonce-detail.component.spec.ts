import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { AnnonceDetailComponent } from './annonce-detail.component';

describe('Annonce Management Detail Component', () => {
  let comp: AnnonceDetailComponent;
  let fixture: ComponentFixture<AnnonceDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AnnonceDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ annonce: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(AnnonceDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(AnnonceDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load annonce on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.annonce).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
