import {Component, OnInit} from '@angular/core';
import {CollectionDto} from "../model/models";
import {CollectionService} from "../service/CollectionService";
import {HttpClient} from "@angular/common/http";

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit{
  collectionDto: CollectionDto;

  constructor(private collectionService : CollectionService,
              private httpClient: HttpClient) {}

  ngOnInit(): void {
    this.collectionService.getNftData().subscribe({
      next: (data) => {
        this.collectionDto = data;
      },
      error: err => console.log("Error fetching Collection data")
    })
  }

  centsToDollarString(cents: number): string {
    const dollars = cents / 100;
    return '$' + dollars.toFixed(2);
  }

  mint() {
    this.httpClient.post("http://localhost:8080/api/create-checkout-session", {
      collectionId: '0x0',
      userId: '0x4'
    }).subscribe();
  }
}
