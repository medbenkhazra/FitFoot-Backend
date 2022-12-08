import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { TerrainDetailComponent } from './terrain-detail.component';

describe('Terrain Management Detail Component', () => {
  let comp: TerrainDetailComponent;
  let fixture: ComponentFixture<TerrainDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TerrainDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ terrain: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(TerrainDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(TerrainDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load terrain on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.terrain).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
