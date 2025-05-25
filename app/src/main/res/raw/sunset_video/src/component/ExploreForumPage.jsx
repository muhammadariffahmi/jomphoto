// ExploreForumPage.jsx
import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import TrendingNow from './TrendingNow';
import TopDiscussions from './TopDiscussions';
import TopSearches from './TopSearches';
import '../styles/ExploreForumPage.css';

const ExploreForumPage = () => {
    const [isModalOpen, setIsModalOpen] = useState(false);

    const openModal = () => setIsModalOpen(true);
    const closeModal = () => setIsModalOpen(false);

    return (
        <main className="explorewrapper">
            <TrendingNow />
            <div className="topdis-topcomsea-group">
                <TopDiscussions />
                <TopSearches />
            </div>
            <div className="navigation-buttons">
                <Link to="/home">
                    <button className="add-discussion">
                        <p className="createpost">HOME</p>
                        <span className="material-symbols-outlined">home</span>
                    </button>
                </Link>
                <Link to="/explore">
                    <button className="add-discussion">
                        <p className="createpost">EXPLORE</p>
                        <span className="material-symbols-outlined">public</span>
                    </button>
                </Link>
                <button className="add-discussion" onClick={openModal}>
                    Create Post
                </button>
            </div>
            {isModalOpen && (
                <div className="modal">
                    <div className="modal-content">
                        <h3>Create Post</h3>
                        <form onSubmit={(e) => {
                            e.preventDefault();
                            closeModal();
                        }}>
                            <label htmlFor="title">Title:</label><br />
                            <input type="text" id="title" name="title" /><br />
                            <label htmlFor="content">Content:</label><br />
                            <textarea id="content" name="content" rows="4" cols="50"></textarea><br />
                            <button type="submit">Create Post</button>
                            <button type="button" className="btn-cancel" onClick={closeModal}>Cancel</button>
                        </form>
                    </div>
                </div>
            )}
        </main>
    );
};

export default ExploreForumPage;
