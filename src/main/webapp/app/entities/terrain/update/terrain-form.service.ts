import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { ITerrain, NewTerrain } from '../terrain.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITerrain for edit and NewTerrainFormGroupInput for create.
 */
type TerrainFormGroupInput = ITerrain | PartialWithRequiredKeyOf<NewTerrain>;

type TerrainFormDefaults = Pick<NewTerrain, 'id'>;

type TerrainFormGroupContent = {
  id: FormControl<ITerrain['id'] | NewTerrain['id']>;
  nom: FormControl<ITerrain['nom']>;
  capaciteParEquipe: FormControl<ITerrain['capaciteParEquipe']>;
  complexe: FormControl<ITerrain['complexe']>;
};

export type TerrainFormGroup = FormGroup<TerrainFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TerrainFormService {
  createTerrainFormGroup(terrain: TerrainFormGroupInput = { id: null }): TerrainFormGroup {
    const terrainRawValue = {
      ...this.getFormDefaults(),
      ...terrain,
    };
    return new FormGroup<TerrainFormGroupContent>({
      id: new FormControl(
        { value: terrainRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      nom: new FormControl(terrainRawValue.nom),
      capaciteParEquipe: new FormControl(terrainRawValue.capaciteParEquipe),
      complexe: new FormControl(terrainRawValue.complexe),
    });
  }

  getTerrain(form: TerrainFormGroup): ITerrain | NewTerrain {
    return form.getRawValue() as ITerrain | NewTerrain;
  }

  resetForm(form: TerrainFormGroup, terrain: TerrainFormGroupInput): void {
    const terrainRawValue = { ...this.getFormDefaults(), ...terrain };
    form.reset(
      {
        ...terrainRawValue,
        id: { value: terrainRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): TerrainFormDefaults {
    return {
      id: null,
    };
  }
}
