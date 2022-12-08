import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'joueur',
        data: { pageTitle: 'fitFootApp.joueur.home.title' },
        loadChildren: () => import('./joueur/joueur.module').then(m => m.JoueurModule),
      },
      {
        path: 'equipe',
        data: { pageTitle: 'fitFootApp.equipe.home.title' },
        loadChildren: () => import('./equipe/equipe.module').then(m => m.EquipeModule),
      },
      {
        path: 'annonce',
        data: { pageTitle: 'fitFootApp.annonce.home.title' },
        loadChildren: () => import('./annonce/annonce.module').then(m => m.AnnonceModule),
      },
      {
        path: 'proprietaire',
        data: { pageTitle: 'fitFootApp.proprietaire.home.title' },
        loadChildren: () => import('./proprietaire/proprietaire.module').then(m => m.ProprietaireModule),
      },
      {
        path: 'quartier',
        data: { pageTitle: 'fitFootApp.quartier.home.title' },
        loadChildren: () => import('./quartier/quartier.module').then(m => m.QuartierModule),
      },
      {
        path: 'complexe',
        data: { pageTitle: 'fitFootApp.complexe.home.title' },
        loadChildren: () => import('./complexe/complexe.module').then(m => m.ComplexeModule),
      },
      {
        path: 'terrain',
        data: { pageTitle: 'fitFootApp.terrain.home.title' },
        loadChildren: () => import('./terrain/terrain.module').then(m => m.TerrainModule),
      },
      {
        path: 'ville',
        data: { pageTitle: 'fitFootApp.ville.home.title' },
        loadChildren: () => import('./ville/ville.module').then(m => m.VilleModule),
      },
      {
        path: 'reservation',
        data: { pageTitle: 'fitFootApp.reservation.home.title' },
        loadChildren: () => import('./reservation/reservation.module').then(m => m.ReservationModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
