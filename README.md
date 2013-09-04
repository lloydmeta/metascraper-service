# Metascraper service [![Build Status](https://travis-ci.org/lloydmeta/metascraper-service.png?branch=master)](https://travis-ci.org/lloydmeta/metascraper-service)

A RESTful Play2-based API application that uses the [Metascraper](https://github.com/lloydmeta/metascraper) library to scrape URLs for metadata. Works rather well because of Play's integration with Akka.

# Heroku

This repository is ready to be pushed / deployed to Heroku as is.

# Running

If you've got Play2 and Scala set up, simply run it as you normally would run your Play applications.

## Vagrant

Vagrant + Chef-solo + Librarian-chef are also set up if you want to run it from a VM. To provision:

1. `bundle install` to download necessary gems
2. `librarian-chef install` to download cookbooks
3. `vagrant up` to provision

## Example usage

### Request
```
Request URL:http://metascraper-staging.herokuapp.com/scrape
Request Method:POST
Content-Type:application/json
Body: {"url":"http://arstechnica.com"}
```

### Response
```json
{
  "title": "Ars Technica",
  "description": "Serving the Technologist for more than a decade. IT news, reviews, and analysis.",
  "url": "http://arstechnica.com",
  "mainImageUrl": "http://cdn.arstechnica.net/wp-content/themes/arstechnica/assets/images/feature-thumbs/eyes-on-enterprise.jpg",
  "imageUrls": [
      "http://cdn.arstechnica.net/wp-content/themes/arstechnica/assets/images/feature-thumbs/eyes-on-enterprise.jpg",
      "http://cdn.arstechnica.net/wp-content/uploads/2013/09/Windows-XP-7-8.1-300x150.png",
      "http://cdn.arstechnica.net/wp-content/uploads/2013/09/ykvcv73j-1378064354-300x150.jpg",
      "http://cdn.arstechnica.net/wp-content/uploads/2012/06/browser_logos-4f7a150-intro-300x150.jpg",
      "http://cdn.arstechnica.net/wp-content/uploads/2012/12/com-x-feat-300x150.jpg"
  ]
}
```
