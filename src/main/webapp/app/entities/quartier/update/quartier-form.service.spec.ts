import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../quartier.test-samples';

import { QuartierFormService } from './quartier-form.service';

describe('Quartier Form Service', () => {
  let service: QuartierFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(QuartierFormService);
  });

  describe('Service methods', () => {
    describe('createQuartierFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createQuartierFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            nom: expect.any(Object),
            ville: expect.any(Object),
          })
        );
      });

      it('passing IQuartier should create a new form with FormGroup', () => {
        const formGroup = service.createQuartierFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            nom: expect.any(Object),
            ville: expect.any(Object),
          })
        );
      });
    });

    describe('getQuartier', () => {
      it('should return NewQuartier for default Quartier initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createQuartierFormGroup(sampleWithNewData);

        const quartier = service.getQuartier(formGroup) as any;

        expect(quartier).toMatchObject(sampleWithNewData);
      });

      it('should return NewQuartier for empty Quartier initial value', () => {
        const formGroup = service.createQuartierFormGroup();

        const quartier = service.getQuartier(formGroup) as any;

        expect(quartier).toMatchObject({});
      });

      it('should return IQuartier', () => {
        const formGroup = service.createQuartierFormGroup(sampleWithRequiredData);

        const quartier = service.getQuartier(formGroup) as any;

        expect(quartier).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IQuartier should not enable id FormControl', () => {
        const formGroup = service.createQuartierFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewQuartier should disable id FormControl', () => {
        const formGroup = service.createQuartierFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
