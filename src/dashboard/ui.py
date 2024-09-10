
import dash_bootstrap_components as dbc
from dash import Input, Output, dcc, html, callback

from src.app import app

sidebar = html.Div(
    [
        html.Img(
            src=app.get_asset_url('ic_logo.png'),
            height="auto",
            width='240px',
        ),
        html.Hr(),
        dbc.Nav(
            [
                dbc.NavLink("Home", href="/", active="exact"),
                dbc.NavLink("Page 1", href="/page-1", active="exact"),
                dbc.NavLink("Page 2", href="/page-2", active="exact"),
            ],
            vertical=True,
            pills=True,
        ),
    ],
    style={
        "position": "fixed",
        "top": 0,
        "left": 0,
        "bottom": 0,
        "width": "wrap",
        "padding": "16px 8px",
        "background-color": "#f8f9fa",
    },
)

dashboard_screen = html.Div([
    dcc.Location(id="url"),
    sidebar,
    html.Div(id="page-content", style={
        "margin-left": '272px',
        "margin-right": "16px",
        "padding": "16px 8px",
    })
])


@callback(Output("page-content", "children"),
          [Input("url", "pathname")])
def render_page_content(pathname):
    if pathname == "/":
        return html.P("This is the content of the home page!")
    elif pathname == "/page-1":
        return html.P("This is the content of page 1. Yay!")
    elif pathname == "/page-2":
        return html.P("Oh cool, this is page 2!")
    # If the user tries to reach a different page, return a 404 message
    return html.Div(
        [
            html.H1("404: Not found", className="text-danger"),
            html.Hr(),
            html.P(f"The pathname {pathname} was not recognised..."),
        ],
        className="p-3 bg-light rounded-3",
    )