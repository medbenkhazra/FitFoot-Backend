import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../ville.test-samples';

import { VilleFormService } from './ville-form.service';

describe('Ville Form Service', () => {
  let service: VilleFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(VilleFormService);
  });

  describe('Service methods', () => {
    describe('createVilleFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createVilleFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            nom: expect.any(Object),
          })
        );
      });

      it('passing IVille should create a new form with FormGroup', () => {
        const formGroup = service.createVilleFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            nom: expect.any(Object),
          })
        );
      });
    });

    describe('getVille', () => {
      it('should return NewVille for default Ville initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createVilleFormGroup(sampleWithNewData);

        const ville = service.getVille(formGroup) as any;

        expect(ville).toMatchObject(sampleWithNewData);
      });

      it('should return NewVille for empty Ville initial value', () => {
        const formGroup = service.createVilleFormGroup();

        const ville = service.getVille(formGroup) as any;

        expect(ville).toMatchObject({});
      });

      it('should return IVille', () => {
        const formGroup = service.createVilleFormGroup(sampleWithRequiredData);

        const ville = service.getVille(formGroup) as any;

        expect(ville).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IVille should not enable id FormControl', () => {
        const formGroup = service.createVilleFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewVille should disable id FormControl', () => {
        const formGroup = service.createVilleFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
