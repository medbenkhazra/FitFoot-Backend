import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IAnnonce, NewAnnonce } from '../annonce.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IAnnonce for edit and NewAnnonceFormGroupInput for create.
 */
type AnnonceFormGroupInput = IAnnonce | PartialWithRequiredKeyOf<NewAnnonce>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IAnnonce | NewAnnonce> = Omit<T, 'heureDebut' | 'heureFin'> & {
  heureDebut?: string | null;
  heureFin?: string | null;
};

type AnnonceFormRawValue = FormValueOf<IAnnonce>;

type NewAnnonceFormRawValue = FormValueOf<NewAnnonce>;

type AnnonceFormDefaults = Pick<NewAnnonce, 'id' | 'heureDebut' | 'heureFin' | 'validation'>;

type AnnonceFormGroupContent = {
  id: FormControl<AnnonceFormRawValue['id'] | NewAnnonce['id']>;
  description: FormControl<AnnonceFormRawValue['description']>;
  heureDebut: FormControl<AnnonceFormRawValue['heureDebut']>;
  heureFin: FormControl<AnnonceFormRawValue['heureFin']>;
  duree: FormControl<AnnonceFormRawValue['duree']>;
  validation: FormControl<AnnonceFormRawValue['validation']>;
  nombreParEquipe: FormControl<AnnonceFormRawValue['nombreParEquipe']>;
  status: FormControl<AnnonceFormRawValue['status']>;
  equipe: FormControl<AnnonceFormRawValue['equipe']>;
  terrain: FormControl<AnnonceFormRawValue['terrain']>;
  responsable: FormControl<AnnonceFormRawValue['responsable']>;
};

export type AnnonceFormGroup = FormGroup<AnnonceFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class AnnonceFormService {
  createAnnonceFormGroup(annonce: AnnonceFormGroupInput = { id: null }): AnnonceFormGroup {
    const annonceRawValue = this.convertAnnonceToAnnonceRawValue({
      ...this.getFormDefaults(),
      ...annonce,
    });
    return new FormGroup<AnnonceFormGroupContent>({
      id: new FormControl(
        { value: annonceRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      description: new FormControl(annonceRawValue.description),
      heureDebut: new FormControl(annonceRawValue.heureDebut),
      heureFin: new FormControl(annonceRawValue.heureFin),
      duree: new FormControl(annonceRawValue.duree),
      validation: new FormControl(annonceRawValue.validation),
      nombreParEquipe: new FormControl(annonceRawValue.nombreParEquipe),
      status: new FormControl(annonceRawValue.status),
      equipe: new FormControl(annonceRawValue.equipe),
      terrain: new FormControl(annonceRawValue.terrain),
      responsable: new FormControl(annonceRawValue.responsable),
    });
  }

  getAnnonce(form: AnnonceFormGroup): IAnnonce | NewAnnonce {
    return this.convertAnnonceRawValueToAnnonce(form.getRawValue() as AnnonceFormRawValue | NewAnnonceFormRawValue);
  }

  resetForm(form: AnnonceFormGroup, annonce: AnnonceFormGroupInput): void {
    const annonceRawValue = this.convertAnnonceToAnnonceRawValue({ ...this.getFormDefaults(), ...annonce });
    form.reset(
      {
        ...annonceRawValue,
        id: { value: annonceRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): AnnonceFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      heureDebut: currentTime,
      heureFin: currentTime,
      validation: false,
    };
  }

  private convertAnnonceRawValueToAnnonce(rawAnnonce: AnnonceFormRawValue | NewAnnonceFormRawValue): IAnnonce | NewAnnonce {
    return {
      ...rawAnnonce,
      heureDebut: dayjs(rawAnnonce.heureDebut, DATE_TIME_FORMAT),
      heureFin: dayjs(rawAnnonce.heureFin, DATE_TIME_FORMAT),
    };
  }

  private convertAnnonceToAnnonceRawValue(
    annonce: IAnnonce | (Partial<NewAnnonce> & AnnonceFormDefaults)
  ): AnnonceFormRawValue | PartialWithRequiredKeyOf<NewAnnonceFormRawValue> {
    return {
      ...annonce,
      heureDebut: annonce.heureDebut ? annonce.heureDebut.format(DATE_TIME_FORMAT) : undefined,
      heureFin: annonce.heureFin ? annonce.heureFin.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
