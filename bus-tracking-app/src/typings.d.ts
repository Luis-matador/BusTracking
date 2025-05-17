declare module 'leaflet.marker.slideto';

interface Marker extends L.Marker {
  slideTo(latlng: L.LatLngExpression, options?: {
    duration?: number,
    keepAtCenter?: boolean
  }): this;
}