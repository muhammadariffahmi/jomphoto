// App.jsx
import React from 'react';
import { Route, Routes } from 'react-router-dom';
import CommentPage from './CommentPage';
import HomePage from './component/HomeForumPage';
import ExplorePage from './component/ExploreForumPage';

function App() {
    return (
        <Routes>
            <Route path="/" element={<CommentPage />} />
            <Route path="/home" element={<HomePage/>} />
            <Route path="/explore" element={<ExplorePage/>} />
        </Routes>
    );
}

export default App;
