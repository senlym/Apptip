Descripción del Proyecto.
Aplicación móvil que permite a los usuarios publicar tips o fragmentos de código útiles y otorgar "likes" a los posts de otros usuarios.
La aplicación facilita la compartición de conocimiento y fomenta la colaboración entre desarrolladores, 
aumentando la calidad del código y las habilidades dentro de un equipo o comunidad.

Instrucciones para configurar el proyecto.
Tiene que tener un VPN para la conexión a la base de datos FireBase en el dispositivo en que se va a utilizar
para ejecutar el proyecto, la PC donde se ejecute no tiene que tener conexión a internet.

Para el diseño se tuvo en cuenta todos los requerimientos para realizar la tarea:

Una Activity para la autenticación de los usuarios.
En esta se utiliza el componente cardview como componente fundamental del layout.
permite dar alta a un usuario nuevo y autenticar a un usuario que esté en nuestra base de datos.
La utenticación de la aplicación es mediante la utenticación FireBase para mayor seguridad y usar las 
bondades de esta plataforma, cuando el usuario se autentica le envía un correo para validar su información.

En la base de datos existe un arbol para Users,  otro para los Tips y uno para los Likes.
Se tomó en cuenta para diseñar el árbol de Users los datos que se necesitaban para la interación del usuario con los tips y los likes.
el árbol de tips tiene relacion con el Users y Likes por id de cada árbol.

Una Activity para mostrar los TIPS en la que se tuvo en cuenta implementar un codeview como elemento para mostrar el código para compartir
y un Multitextview para la descripción del tip. En esta se observa una pequeña parte del código y la descripción, para en otra activity llamada detalles 
mostrar el texto de código o el texto de descripción completamente. Se optó por un botón flotante para mostrar la activity de edición de los tips.
Esta activity tiene implementada una clase que es la encargada de la carga de los tips de la base de datos para insertarlos en un linear layout.
Las actualizaciones entre activity se realiza por broadcast y recibers, tambien con los bundles. Se implementó un filtro para buscar por el título de los tips.
Por defecto los usuarios al entrar a la aplicación van a ver los tips que tengan mayor cantidad de likes y pueden hacer búsquedas para mostrar un tema determinado.
Así se realiza la calificación del contenido.

La Activity para la edición del tip se utilizó el codeview que puede ser configurable y el multitextview para la descripción, tres botones flotantes
para hacer la interfaz mas amigable al usuario. 
Uno de los retos impuestos en el desarrollo de la aplicación fue interactuar con firebase, este es mi primer proyecto en esta plataforma. 

Se diseño un esquema para conectarse a una base de datos de firebase local.
pero con el CLI de firebase después de realizar varias pruebas no fue posible pero se deja todo el esquema para 
si se logra la conexión. 




