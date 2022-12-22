import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../equipe.test-samples';

import { EquipeFormService } from './equipe-form.service';

describe('Equipe Form Service', () => {
  let service: EquipeFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(EquipeFormService);
  });

  describe('Service methods', () => {
    describe('createEquipeFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createEquipeFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            joueurs: expect.any(Object),
          })
        );
      });

      it('passing IEquipe should create a new form with FormGroup', () => {
        const formGroup = service.createEquipeFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            joueurs: expect.any(Object),
          })
        );
      });
    });

    describe('getEquipe', () => {
      it('should return NewEquipe for default Equipe initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createEquipeFormGroup(sampleWithNewData);

        const equipe = service.getEquipe(formGroup) as any;

        expect(equipe).toMatchObject(sampleWithNewData);
      });

      it('should return NewEquipe for empty Equipe initial value', () => {
        const formGroup = service.createEquipeFormGroup();

        const equipe = service.getEquipe(formGroup) as any;

        expect(equipe).toMatchObject({});
      });

      it('should return IEquipe', () => {
        const formGroup = service.createEquipeFormGroup(sampleWithRequiredData);

        const equipe = service.getEquipe(formGroup) as any;

        expect(equipe).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IEquipe should not enable id FormControl', () => {
        const formGroup = service.createEquipeFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewEquipe should disable id FormControl', () => {
        const formGroup = service.createEquipeFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
