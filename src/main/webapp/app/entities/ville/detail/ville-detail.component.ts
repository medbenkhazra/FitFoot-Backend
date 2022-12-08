import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IVille } from '../ville.model';

@Component({
  selector: 'jhi-ville-detail',
  templateUrl: './ville-detail.component.html',
})
export class VilleDetailComponent implements OnInit {
  ville: IVille | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ ville }) => {
      this.ville = ville;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
