
import dash_bootstrap_components as dbc
from click import style
from dash import Input, Output, html, callback, State

from src.app import app, color_mode_switch

email_input = html.Div([
        dbc.FormFloating([
            dbc.Input(id="example-email", type="email", placeholder="Enter email"),
            dbc.Label("Email", html_for="example-email"),
        ]),
        dbc.FormText("Are you on email? You simply have to be these days", color="secondary", style={'display': 'none'}),
    ],
    className="mb-3",
)

password_input = html.Div([
        dbc.FormFloating([
            dbc.Input(
                type="password",
                id="example-password",
                placeholder="Enter password",
            ),
            dbc.Label("Password", html_for="example-password"),
        ]),
        dbc.FormText("A password stops mean people taking your stuff", color="secondary", style={'display': 'none'}),
    ],
    className="mb-3",
)

login_screen = html.Div([
    color_mode_switch,
    dbc.CardGroup(
        [
            dbc.Card(
                [
                    dbc.CardImg(
                        src=app.get_asset_url('light-bg.png'),
                        top=True,
                        style={"opacity": 0.2, 'height': '100vh', 'width': '50vw'},
                    ),
                    dbc.CardImgOverlay(
                        dbc.CardBody(
                            [
                                html.H1("Welcome to", className="card-title",
                                        style={'width': 'auto', 'margin-left': '64px', 'margin-end': '44px'}),
                                html.Img(src=app.get_asset_url('ic_logo.png'), style={'width': '100%'}),
                                html.H5("A whole new productive journey starts right here",className="card-text",
                                        style={'width': 'auto', 'margin-left': '44px', 'margin-end': '36px'}),
                            ],
                        ),
                        style={'align-content': 'center'},
                    )
                ],
                class_name="align-items-center",
                outline=False,
                style={'border': 'none'},
            ),
            dbc.Card(
                dbc.CardBody(
                    [
                        dbc.Button(
                            [
                                html.I(className="fa-brands fa-google"),
                                "\tContinue with Google"
                            ],
                            color="info",
                            outline=True,
                            className="mt-auto",
                            size='lg',
                            style={'width': '32vw'},
                        ),
                        html.Div([
                            html.Hr(),
                            html.P(' Or '),
                            html.Hr(),
                        ], style={'justify-content': 'center', 'display': 'flex', 'margin-bottom':'18px', 'margin-top':'28px'}),
                        email_input,
                        password_input,
                        dbc.Row(
                            [
                                dbc.Col(
                                    dbc.Button(
                                        "Password Recovery",
                                        color="secondary",
                                        className="mt-auto",
                                        size='sm',
                                        href='/password-recovery',
                                        outline=True,
                                        style={'width': 'auto', 'border': 'none'},
                                    ),
                                    width='auto'
                                ),
                                dbc.Col(
                                    dbc.Button(
                                        "Register",
                                        color="secondary",
                                        className="mt-auto",
                                        size='sm',
                                        href='/register',
                                        outline=True,
                                        style={'width': 'auto', 'border': 'none'},
                                    ),
                                    width='auto'
                                ),
                            ],
                            justify='between',
                            style={'margin-bottom': '22px'},
                        ),
                        dbc.Button(
                            "Login",
                            color="primary",
                            className="mt-auto",
                            size='lg',
                            style={'width': '32vw', 'border': 'none'},
                        ),
                    ],
                    style={'width': 'auto', 'height': 'auto'},
                ),
                class_name="align-items-center",
                outline=False,
                style={'border': 'none'},
            )
        ],
        style={'height': '100vh', 'width': '100vw'},
        class_name="align-items-center",
    )
])