import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { QuartierDetailComponent } from './quartier-detail.component';

describe('Quartier Management Detail Component', () => {
  let comp: QuartierDetailComponent;
  let fixture: ComponentFixture<QuartierDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [QuartierDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ quartier: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(QuartierDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(QuartierDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load quartier on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.quartier).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
