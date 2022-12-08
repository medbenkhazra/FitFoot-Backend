import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IEquipe } from '../equipe.model';

@Component({
  selector: 'jhi-equipe-detail',
  templateUrl: './equipe-detail.component.html',
})
export class EquipeDetailComponent implements OnInit {
  equipe: IEquipe | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ equipe }) => {
      this.equipe = equipe;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
