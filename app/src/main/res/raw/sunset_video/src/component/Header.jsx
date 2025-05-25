import React from 'react';
import '../styles/navbar.css';

function Header() {
    return ( 
        <header>
        <nav className="navbar navbar-expand-lg text-white ft">
            <div className="container-fluid">
                <a className="navbar-brand text-white rounded" href="#">MM LABELS GROUP</a>
        
                <button className="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarScroll" aria-controls="navbarScroll" aria-expanded="false" aria-label="Toggle navigation" style="border-color: white;">
                    <span className="navbar-toggler-icon"></span>
                </button>
        
                <div className="collapse navbar-collapse" id="navbarScroll">
                    <ul className="navbar-nav me-auto my-2 my-lg-0 navbar-nav-scroll justify-content-between" style="--bs-scroll-height: 100px;">
                        <li className="nav-item">
                            <a className="nav-link active text-white rounded" aria-current="page" href="Home.html">HOME</a>
                        </li>
                        <li className="nav-item">
                            <a className="nav-link text-white rounded" href="#">ARTISTS</a>
                        </li>
                        <li className="nav-item">
                            <a className="nav-link text-white rounded" href="#">BOOK</a>
                        </li>
                        <li className="nav-item">
                            <a className="nav-link text-white rounded" href="#">EVENTS</a>
                        </li>
                        <li className="nav-item dropdown">
                            <a className="nav-link dropdown-toggle text-white rounded" href="#" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                                SHOP
                            </a>
                            <ul className="dropdown-menu bg-dark text-white">
                                <li><a className="dropdown-item text-white" href="shop.html">MERCHANDISE</a></li>
                                <li><a className="dropdown-item text-white" href="#">MY CART</a></li>
                                <li><a className="dropdown-item text-white" href="#">PURCHASE HISTORY</a></li>
                            </ul>
                        </li>
                        <li className="nav-item">
                            <a className="nav-link text-white rounded" href="#">FORUM</a>
                        </li>
                    </ul>
                    <form className="d-flex" role="search">
                        <input className="form-control me-2 bg-transparent text-white" type="search" placeholder="" aria-label="Search"/>
                        <button className="btn btn-outline-danger" type="submit">Search</button>
                    </form>
                </div>
            </div>
        </nav>
        
    </header>
     );
}

export default Header;