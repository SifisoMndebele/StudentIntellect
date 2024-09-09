import dash_bootstrap_components as dbc
import pandas as pd
from dash import html, dcc, dash_table
import plotly.express as px
# from backend import store_names_df, store_performance_df, total_sales_df, total_purchase_df, popular_products_df, reason_df, \
#     sales_amount_df, engine

# money = total_sales_df.total
# sales_fig = px.line(x=sales_amount_df.salesdate, y=sales_amount_df.totalsalesamount)
# sales_fig.update_layout(template='plotly_dark',
#                       plot_bgcolor='rgba(0, 0, 0, 0)',
#                       paper_bgcolor='rgba(0, 0, 0, 0)', )

# reason = """
#     SELECT sr.Name FROM Sales.SalesOrderHeader soh
#     JOIN Sales.SalesOrderHeaderSalesReason sohsr ON soh.SalesOrderID = sohsr.SalesOrderID
#     JOIN Sales.SalesReason sr ON sohsr.SalesReasonID = sr.SalesReasonID
# """
# reason_df = pd.read_sql_query(reason, engine)
# reason_fig = px.pie(reason_df)

home_content = html.Div([
    dbc.Navbar(
        color="dark",
        dark=True,
        children=dbc.NavbarBrand("Dashboard", className="ms-4"),
    ),
    dbc.Row(
        className="g-2",
        style={"margin": "1rem"},
        children=[

            dbc.Col([
                dbc.Row([
                    dbc.Col([
                        dbc.Card([
                            dbc.CardBody([
                                html.H5("Total Purchases"),
                                # dbc.Label(total_purchase_df.total, id="total-amount", style={'height': '2.5rem'}),
                            ])
                        ], className="border-0",
                            style={"background-color": "#f8f9fa09"})
                    ]),
                    dbc.Col([
                        dbc.Card([
                            dbc.CardBody([
                                html.H5("Total Sales (R)"),
                                # dbc.Label(money, id="total-amount", style={'height': '2.5rem'}),
                            ])
                        ], className="border-0",
                            style={"background-color": "#f8f9fa09"})
                    ]),
                ], className="g-1", ),
                # dbc.Row(dcc.Graph(figure=sales_fig, style={'height': '20rem', 'width': '42rem', }, )),

                dcc.Dropdown(id='product_dropdown',
                             options=['top', 'Middle', 'Bottom'],
                             value='top',
                             clearable=False,
                             style={'height': 'auto', 'width': '50%', 'border': '0',
                                    'size': 'small', "background-color": "#0000"}),
                dbc.Row(dcc.Graph(id='product_sold',
                                  style={'height': '20rem', 'width': '42rem', }, )),

            ], className="g-1", )
        ]
    ),
    # dbc.Row(dcc.Graph(id='bars-chart', style={'height': '20rem', 'width': '42rem', }, )),
])

store_content = html.Div([
    dbc.Navbar(
        color="dark",
        dark=True,
        children=[
            dbc.NavbarBrand("Shop Information", className="ms-4"),
            dbc.Container(
                children=html.Div(
                    className="py-2, background-0",
                    children=dbc.Select(
                        # store_names_df['name'],
                        # store_names_df['name'][0],
                        id="store-dropdown-menu",
                    ),
                ),
            ),
        ]
    ),
    dbc.Col([
        dash_table.DataTable(
            id='table',
            # columns=[{"name": i, "id": i} for i in store_performance_df.columns],
            # data=store_performance_df.to_dict('records'),
            sort_action="native",
            style_table={'overflowX': 'scroll'},
            style_header={'fontWeight': 'bold'},
            style_data={'whiteSpace': 'normal', 'height': 'auto'},
            page_size=10,
        )
    ], style={"margin": "1rem"}, ),

])

# popular_products_fig = px.bar(popular_products_df, x='total_numberof_orders', y='name', orientation='h',
#                               title='Most Popular Products')

# Adjusting the width of the bars and adding space between them
# popular_products_fig.update_layout(width=1000, height=900, margin=dict(l=100, r=100, t=50, b=50), bargap=0.2)

# # Update hover information
# popular_products_fig.update_traces(hovertemplate='Product: %{y}<br>Total Orders: %{x}<br>Profit Margin: %{text}',
#                                    text=popular_products_df['profit_margin'])

products_content = html.Div([
    dbc.Navbar(
        color="dark",
        dark=True,
        children=[
            dbc.NavbarBrand("Products", className="ms-4"),
        ]
    ),
    dbc.Col([
        # dcc.Graph(id='popular-products-graph', figure=popular_products_fig,
        #           style={"background-color": "#00000000"},),
        html.Div(id="products-list"),
    ], style={"margin": "1rem"}, ),

])
