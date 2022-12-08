import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IQuartier } from '../quartier.model';

@Component({
  selector: 'jhi-quartier-detail',
  templateUrl: './quartier-detail.component.html',
})
export class QuartierDetailComponent implements OnInit {
  quartier: IQuartier | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ quartier }) => {
      this.quartier = quartier;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
