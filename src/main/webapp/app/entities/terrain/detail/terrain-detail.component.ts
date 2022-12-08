import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ITerrain } from '../terrain.model';

@Component({
  selector: 'jhi-terrain-detail',
  templateUrl: './terrain-detail.component.html',
})
export class TerrainDetailComponent implements OnInit {
  terrain: ITerrain | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ terrain }) => {
      this.terrain = terrain;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
