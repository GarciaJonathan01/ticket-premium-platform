using System.Web.Http;

namespace FederacionFutbol_API
{
    public static class WebApiConfig
    {
        public static void Register(HttpConfiguration config)
        {
            config.MapHttpAttributeRoutes();

            config.Routes.MapHttpRoute(
                name: "DefaultApi",
                routeTemplate: "api/{controller}/{action}/{id}",
                defaults: new { id = RouteParameter.Optional }
            );

            // Formato JSON por defecto
            config.Formatters.Remove(config.Formatters.XmlFormatter);
        }
    }
}
