import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../complexe.test-samples';

import { ComplexeFormService } from './complexe-form.service';

describe('Complexe Form Service', () => {
  let service: ComplexeFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ComplexeFormService);
  });

  describe('Service methods', () => {
    describe('createComplexeFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createComplexeFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            nom: expect.any(Object),
            longitude: expect.any(Object),
            latitude: expect.any(Object),
            quartier: expect.any(Object),
            proprietaire: expect.any(Object),
          })
        );
      });

      it('passing IComplexe should create a new form with FormGroup', () => {
        const formGroup = service.createComplexeFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            nom: expect.any(Object),
            longitude: expect.any(Object),
            latitude: expect.any(Object),
            quartier: expect.any(Object),
            proprietaire: expect.any(Object),
          })
        );
      });
    });

    describe('getComplexe', () => {
      it('should return NewComplexe for default Complexe initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createComplexeFormGroup(sampleWithNewData);

        const complexe = service.getComplexe(formGroup) as any;

        expect(complexe).toMatchObject(sampleWithNewData);
      });

      it('should return NewComplexe for empty Complexe initial value', () => {
        const formGroup = service.createComplexeFormGroup();

        const complexe = service.getComplexe(formGroup) as any;

        expect(complexe).toMatchObject({});
      });

      it('should return IComplexe', () => {
        const formGroup = service.createComplexeFormGroup(sampleWithRequiredData);

        const complexe = service.getComplexe(formGroup) as any;

        expect(complexe).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IComplexe should not enable id FormControl', () => {
        const formGroup = service.createComplexeFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewComplexe should disable id FormControl', () => {
        const formGroup = service.createComplexeFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
