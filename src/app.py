import dash_bootstrap_components as dbc
from dash import Dash, html, clientside_callback, Output, Input

app = Dash(__name__, external_stylesheets=[dbc.themes.MINTY, dbc.icons.FONT_AWESOME])
app.title = 'Student Intellect'
app._favicon = 'favicon.png'
app.config.suppress_callback_exceptions = True

color_mode_switch =  html.Span(
    [
        dbc.Label(className="fa fa-moon", html_for="switch"),
        dbc.Switch( id="switch", value=True, className="d-inline-block ms-1", persistence=True),
        dbc.Label(className="fa fa-sun", html_for="switch"),
    ]
)

clientside_callback(
    """
    (switchOn) => {
       document.documentElement.setAttribute("data-bs-theme", switchOn ? "light" : "dark");
       return window.dash_clientside.no_update
    }
    """,
    Output("switch", "id"),
    Input("switch", "value")
)