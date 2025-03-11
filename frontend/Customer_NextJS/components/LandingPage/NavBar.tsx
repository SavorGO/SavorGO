import { MenuItemType } from '@/lib/interface'
import React from 'react'
import { DesktopMenu, MobileMenu } from '../Menu/Menu'

const NavBar = () => {
    const navLinks: MenuItemType[] = [
        {
            id: 'link1',
            title: 'Địa điểm',
            url: '/location'
        },
        {
            id: 'link2',
            title: 'Thực đơn',
            url: '/menu'
        },
        {
            id: 'link3',
            title: 'Đặt bàn',
            url: '/reservations'
        },{
            id: 'link4',
            title: 'Không gian',
            url: '/space'
        },{
            id: 'link5',
            title: 'Tin tức',
            url: '/news'
        },
    ]
    return (
        <>
            {/* Desktop Menu */}
            <nav className='hidden md:grid grid-cols-[auto,1fr,auto] gap-4 items-center w-full'>
  <DesktopMenu links={navLinks} />
</nav>

            {/* Mobile Menu */}
            <nav className='md:hidden flex justify-between items-center'>
                <MobileMenu links={navLinks}/>
            </nav>
        </>
    )
}

export default NavBar