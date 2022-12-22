import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../annonce.test-samples';

import { AnnonceFormService } from './annonce-form.service';

describe('Annonce Form Service', () => {
  let service: AnnonceFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AnnonceFormService);
  });

  describe('Service methods', () => {
    describe('createAnnonceFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createAnnonceFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            description: expect.any(Object),
            heureDebut: expect.any(Object),
            heureFin: expect.any(Object),
            duree: expect.any(Object),
            validation: expect.any(Object),
            nombreParEquipe: expect.any(Object),
            status: expect.any(Object),
            equipe: expect.any(Object),
            terrain: expect.any(Object),
            responsable: expect.any(Object),
          })
        );
      });

      it('passing IAnnonce should create a new form with FormGroup', () => {
        const formGroup = service.createAnnonceFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            description: expect.any(Object),
            heureDebut: expect.any(Object),
            heureFin: expect.any(Object),
            duree: expect.any(Object),
            validation: expect.any(Object),
            nombreParEquipe: expect.any(Object),
            status: expect.any(Object),
            equipe: expect.any(Object),
            terrain: expect.any(Object),
            responsable: expect.any(Object),
          })
        );
      });
    });

    describe('getAnnonce', () => {
      it('should return NewAnnonce for default Annonce initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createAnnonceFormGroup(sampleWithNewData);

        const annonce = service.getAnnonce(formGroup) as any;

        expect(annonce).toMatchObject(sampleWithNewData);
      });

      it('should return NewAnnonce for empty Annonce initial value', () => {
        const formGroup = service.createAnnonceFormGroup();

        const annonce = service.getAnnonce(formGroup) as any;

        expect(annonce).toMatchObject({});
      });

      it('should return IAnnonce', () => {
        const formGroup = service.createAnnonceFormGroup(sampleWithRequiredData);

        const annonce = service.getAnnonce(formGroup) as any;

        expect(annonce).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IAnnonce should not enable id FormControl', () => {
        const formGroup = service.createAnnonceFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewAnnonce should disable id FormControl', () => {
        const formGroup = service.createAnnonceFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
