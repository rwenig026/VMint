import {Injectable, OnInit} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";

@Injectable({
  providedIn:'root'
})
export class CollectionService {
  constructor(private httpClient : HttpClient) { }

  getNftData(): Observable<any> {
    return this.httpClient.get("http://localhost:8080/api/collection");
  }
}
