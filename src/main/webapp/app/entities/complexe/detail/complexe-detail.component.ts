import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IComplexe } from '../complexe.model';

@Component({
  selector: 'jhi-complexe-detail',
  templateUrl: './complexe-detail.component.html',
})
export class ComplexeDetailComponent implements OnInit {
  complexe: IComplexe | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ complexe }) => {
      this.complexe = complexe;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
