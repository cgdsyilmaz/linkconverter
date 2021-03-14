# TrendyolCase-CagdasYilmaz

## Docker usage
  `docker-compose up --build`
  
## Endpoints
Use HTTP POST request with body as JSON Object

### From WebURL to Deeplink
  `/to_deeplink`
  
  #### Request URL:
  `http://localhost:8080/to_deeplink`
  #### Body:
  ```
    {
      "link": "https://www.trendyol.com/casio/saat-p-1925865?boutiqueId=439892&merchantId=105064"
    }
  ```
  #### Response body:
  ```
    {
      "link": "ty://?Page=Product&ContentId=1925865&CampaignId=439892&MerchantId=105064"
    }
  ```
  
  ### From WebURL to Deeplink
  `/to_web_url`
  
  #### Request URL:
  `http://localhost:8080/to_web_url`
  #### Body:
  ```
    {
      "link": "ty://?Page=Product&ContentId=1925865&CampaignId=439892&MerchantId=105064"
    }
  ```
  
   #### Response body:
  ```
    {
      "link": "https://www.trendyol.com/brand/namep-1925865?boutiqueId=439892&merchantId=105064"
    }
  ```
