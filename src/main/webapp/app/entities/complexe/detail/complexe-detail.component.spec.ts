import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ComplexeDetailComponent } from './complexe-detail.component';

describe('Complexe Management Detail Component', () => {
  let comp: ComplexeDetailComponent;
  let fixture: ComponentFixture<ComplexeDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ComplexeDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ complexe: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(ComplexeDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ComplexeDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load complexe on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.complexe).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
