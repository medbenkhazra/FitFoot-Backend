import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { VilleDetailComponent } from './ville-detail.component';

describe('Ville Management Detail Component', () => {
  let comp: VilleDetailComponent;
  let fixture: ComponentFixture<VilleDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [VilleDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ ville: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(VilleDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(VilleDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load ville on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.ville).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
