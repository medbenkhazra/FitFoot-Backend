import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../terrain.test-samples';

import { TerrainFormService } from './terrain-form.service';

describe('Terrain Form Service', () => {
  let service: TerrainFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TerrainFormService);
  });

  describe('Service methods', () => {
    describe('createTerrainFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createTerrainFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            nom: expect.any(Object),
            capaciteParEquipe: expect.any(Object),
            complexe: expect.any(Object),
          })
        );
      });

      it('passing ITerrain should create a new form with FormGroup', () => {
        const formGroup = service.createTerrainFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            nom: expect.any(Object),
            capaciteParEquipe: expect.any(Object),
            complexe: expect.any(Object),
          })
        );
      });
    });

    describe('getTerrain', () => {
      it('should return NewTerrain for default Terrain initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createTerrainFormGroup(sampleWithNewData);

        const terrain = service.getTerrain(formGroup) as any;

        expect(terrain).toMatchObject(sampleWithNewData);
      });

      it('should return NewTerrain for empty Terrain initial value', () => {
        const formGroup = service.createTerrainFormGroup();

        const terrain = service.getTerrain(formGroup) as any;

        expect(terrain).toMatchObject({});
      });

      it('should return ITerrain', () => {
        const formGroup = service.createTerrainFormGroup(sampleWithRequiredData);

        const terrain = service.getTerrain(formGroup) as any;

        expect(terrain).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ITerrain should not enable id FormControl', () => {
        const formGroup = service.createTerrainFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewTerrain should disable id FormControl', () => {
        const formGroup = service.createTerrainFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
